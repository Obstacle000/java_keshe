package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.*;

@Service
public class CompetitionSignupService {
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private StudentCompetitionSignupRepository signUpRepository;

    @Autowired
    private CompetitionScoreRepository competitionScoreRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PersonRepository personRepository;


    public DataResponse signup(@Valid DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("personId");
        Integer competitionId = dataRequest.getInteger("competitionId");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;

        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }else
        {
            return CommonMethod.getReturnMessageError("请使用学生账号报名");
        }


        Optional<Competition> CompetitionOpt = competitionRepository.findById(competitionId);
        if (CompetitionOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("竞赛信息不存在");
        }
        Competition competition = CompetitionOpt.get();

        // 时间范围判断
        Date now = new Date();
        if (now.before(competition.getStartTime()) || now.after(competition.getEndTime())) {
            return CommonMethod.getReturnMessageError("当前不在报名时间范围内");
        }
        // 判断是否已经报名
        Optional<CompetitionSignup> ss = signUpRepository.findByStudentPersonIdAndCompetitionCompetitionId(student.getPersonId(),competitionId);

        if (ss.isPresent()) {
            Boolean isSignedUp = ss.get().getStatus();
            if(isSignedUp) {
                // 学生报名过,且在报名中的状态
                return CommonMethod.getReturnMessageError("学生已报名该竞赛活动");
            }
            else if(!isSignedUp) {
                // 学生报名后取消报名了,记录还在,此时需要改成true
                ss.get().setStatus(true);
                ss.get().setSignupTime(new Date());

                signUpRepository.save(ss.get());
                return CommonMethod.getReturnMessageOK("报名成功");

            }
        }

        // 学生第一次报名

        CompetitionSignup signup = new CompetitionSignup();
        signup.setStudent(student);
        signup.setCompetition(competition);
        signup.setStatus(true);
        signup.setSignupTime(new Date());
        try {
            signUpRepository.save(signup);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError(e.getMessage());
        }

        return CommonMethod.getReturnMessageOK("报名成功");

    }



    public DataResponse cancelSignup(@Valid DataRequest dataRequest){
        Integer competitionId = dataRequest.getInteger("competitionId");

        Integer personId = dataRequest.getInteger("personId");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }
        Optional<CompetitionSignup> signupOpt = signUpRepository.findByStudentPersonIdAndCompetitionCompetitionId(student.getPersonId(), competitionId);
        if (signupOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("报名信息不存在");
        }

        // 有分数不能取消
        CompetitionScoreRecord competitionScoreRecord = competitionScoreRepository.findByCompetitionCompetitionId(competitionId);
        if (competitionScoreRecord != null) {
            return CommonMethod.getReturnMessageError("以评分不能取消报名");
        }

        CompetitionSignup competitionSignup = signupOpt.get();

        competitionSignup.setStatus(false);
        signUpRepository.save(competitionSignup);
        return CommonMethod.getReturnMessageOK("取消报名成功");
    }

    public DataResponse getSignupList(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");

        if (competitionId == null) {
            return CommonMethod.getReturnMessageError("缺少竞赛ID");
        }

        List<CompetitionSignup> signupList = signUpRepository.findByCompetitionCompetitionId(competitionId);
        if(signupList.isEmpty())
        {
            return CommonMethod.getReturnMessageError("暂无学生报名");
        }
        Map<String,Object> m;
        List<Map<String,Object>> dataList = new ArrayList<>();

        for (CompetitionSignup signup : signupList) {
            m = new HashMap<>();
            Student student = signup.getStudent();
            String num = student.getPerson().getNum();
            Date signupTime = signup.getSignupTime();
            CompetitionScoreRecord competitionScoreRecord = competitionScoreRepository.findByCompetitionCompetitionId(competitionId);

            m.put("num",num);
            m.put("signupTime",signupTime);

            m.put("name",student.getPerson().getName());
            if(competitionScoreRecord != null)
            {
                m.put("score",competitionScoreRecord.getScore());
            }


            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

}
