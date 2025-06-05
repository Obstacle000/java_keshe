package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CompetitionService {
    @Autowired
    CompetitionRepository competitionRepository;
    @Autowired
    StudentCompetitionSignupRepository studentSignUpRepository;
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    CompetitionScoreRepository competitionScoreRepository;

    public DataResponse getCompetitionList(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }

        List<Competition> lists = competitionRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Competition competition : lists) {
            m = new HashMap<>();
            long count = studentSignUpRepository.countByCompetitionId(competition.getCompetitionId());

            // 默认未报名
            m.put("isSignedUp", false);

            // 如果是学生，则去查询报名状态
            if (student != null) {
                CompetitionSignup as = studentSignUpRepository.findByStudentAndCompetition(student, competition);
                if (as != null && Boolean.TRUE.equals(as.getStatus())) {
                    m.put("isSignedUp", true);
                }
            }

            m.put("signupCount", count);
            m.put("competitionId", competition.getCompetitionId());
            m.put("title", competition.getTitle());
            m.put("description", competition.getDescription());
            m.put("startTime", competition.getStartTime() == null ? "" : sdf.format(competition.getStartTime()));
            m.put("endTime", competition.getEndTime() == null ? "" : sdf.format(competition.getEndTime()));

            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getNoticeList(@Valid DataRequest dataRequest) {
        List<Notice> notices = noticeRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Notice notice : notices) {
            Map<String, Object> m = new HashMap<>();
            m.put("noticeId", notice.getNoticeId());
            m.put("title", notice.getTitle()); // 或者加上时间等信息
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getCompetitionContent(@Valid DataRequest dataRequest) {
        // 根据活动id去通知表里找content
        Integer competitionId = dataRequest.getInteger("competitionId");
        Optional<Competition> byId = competitionRepository.findById(competitionId);
        Competition competition= null;
        if (byId.isPresent()) {
            competition = byId.get();
        }
        Notice notice = competition.getNotice();
        if (notice == null) {
            return CommonMethod.getReturnMessageError("该活动没有关联通知");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("noticeId", notice.getNoticeId());
        m.put("title", notice.getTitle());
        m.put("content", notice.getContent());
        m.put("createTime", notice.getCreateTime());
        return CommonMethod.getReturnData(m);
    }

    public DataResponse addCompetition(@Valid DataRequest dataRequest) {
        String title = dataRequest.getString("title");
        String description = dataRequest.getString("description");
        Date startTime = dataRequest.getDate("startTime");
        Date endTime = dataRequest.getDate("endTime");
        Integer noticeId = dataRequest.getInteger("noticeId");

        // 时间合法性判断
        if (startTime == null || endTime == null) {
            return CommonMethod.getReturnMessageError("开始时间或结束时间不能为空");
        }
        if (!endTime.after(startTime)) {
            return CommonMethod.getReturnMessageError("结束时间必须在开始时间之后");
        }

        Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
        if (noticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("通知不存在");
        }



        Competition competition = new Competition();
        competition.setTitle(title);
        competition.setDescription(description);
        competition.setStartTime(startTime);
        competition.setEndTime(endTime);
        competition.setNotice(noticeOpt.get());

        competitionRepository.save(competition);


        return CommonMethod.getReturnMessageOK("添加成功");
    }



    public DataResponse deleteCompetition(@Valid DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        Optional<Competition> competitionOpt = competitionRepository.findById(competitionId);
        Competition competition = null;

        if (competitionOpt.isPresent()) {
            competition = competitionOpt.get();
        }

        // 先删除报名记录（如果有的话）
        studentSignUpRepository.deleteByCompetition(competition);

        competitionRepository.deleteById(competitionId);
        return CommonMethod.getReturnMessageOK("删除成功");
    }


    public DataResponse updateCompetition(@Valid DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        Optional<Competition> competitionOpt = competitionRepository.findById(competitionId);

        if (competitionOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("活动不存在");
        }

        Competition competition = competitionOpt.get();

        competition.setTitle(dataRequest.getString("title"));
        competition.setDescription(dataRequest.getString("description"));
        competition.setStartTime(dataRequest.getDate("startTime"));
        competition.setEndTime(dataRequest.getDate("endTime"));

        Integer noticeId = dataRequest.getInteger("noticeId");
        Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
        if (noticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("关联通知不存在");
        }

        competition.setNotice(noticeOpt.get());

        competitionRepository.save(competition);
        return CommonMethod.getReturnMessageOK("更新成功");
    }



    // 前端老师 - 竞赛中心-竞赛信息 - 学生详情 - 打分按钮
    public DataResponse CompetitionScore(@Valid DataRequest dataRequest){
        Integer competitionId = dataRequest.getInteger("competitionId");
        String num = dataRequest.getString("num");
        Integer score = dataRequest.getInteger("score");

        // 检查竞赛是否存在
        Optional<Competition> competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("竞赛不存在");
        }
        Competition competition = competitionOpt.get();

        // 检查学生是否存在
        Optional<Student> studentOpt = studentRepository.findByPersonNum(num);
        if (studentOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }

        Student student = studentOpt.get();

        // 检查是否已经存在该学生的成绩记录
        Optional<CompetitionScoreRecord> existingScoreOpt = competitionScoreRepository.findByCompetitionAndStudent(competition, student);
        if (existingScoreOpt.isPresent()) {
            CompetitionScoreRecord existingScoreRecord = existingScoreOpt.get();
            existingScoreRecord.setScore(score);
            competitionScoreRepository.save(existingScoreRecord);
            return CommonMethod.getReturnMessageOK("该学生的成绩记录已更新");
        }

        // 创建成绩记录
        CompetitionScoreRecord scoreRecord = new CompetitionScoreRecord();
        scoreRecord.setCompetition(competition);
        scoreRecord.setStudent(student);
        scoreRecord.setScore(score);

        // 保存成绩记录
        competitionScoreRepository.save(scoreRecord);

        return CommonMethod.getReturnMessageOK("成绩记录添加成功");
    }

    // 学生 - 竞赛中心 - 竞赛记录
    public DataResponse getRecord(@Valid DataRequest dataRequest) {
        // 竞赛title,报名时间,分数
        Integer personId = dataRequest.getInteger("personId");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;

        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }

        List<Competition> all = competitionRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Competition competition : all) {
            Map<String, Object> data = new HashMap<>();
            Optional<CompetitionScoreRecord> byCompetitionAndStudent = competitionScoreRepository.findByCompetitionAndStudent(competition, student);
            CompetitionScoreRecord scoreRecord = null;
            if (byCompetitionAndStudent.isPresent()) {
                scoreRecord = byCompetitionAndStudent.get();
            }

            CompetitionSignup cs = studentSignUpRepository.findByCompetitionAndStudent(competition,student);

            data.put("title", competition.getTitle());
            data.put("signupTime", cs.getSignupTime());
            data.put("score", scoreRecord.getScore());

            dataList.add(data);
        }
        return CommonMethod.getReturnData(dataList);
    }


    //

}



