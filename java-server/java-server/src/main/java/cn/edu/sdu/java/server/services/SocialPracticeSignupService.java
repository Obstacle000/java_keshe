package cn.edu.sdu.java.server.services;


import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.repositorys.SocialPracticeRepository;
import cn.edu.sdu.java.server.repositorys.SocialPracticeSignupRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SocialPracticeSignupService {

    @Autowired
    private SocialPracticeRepository socialPracticeRepository;

    @Autowired
    private SocialPracticeSignupRepository socialPracticeSignupRepository;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PersonRepository personRepository;


    public DataResponse signup(@Valid DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("personId");
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;

        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }else
        {
            return CommonMethod.getReturnMessageError("请使用学生账号报名");
        }


        Optional<SocialPractice> SocialPracticeOpt = socialPracticeRepository.findById(socialPracticeId);
        if (SocialPracticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("社会实践不存在");
        }
        SocialPractice socialPractice = SocialPracticeOpt.get();

        // 时间范围判断
        Date now = new Date();
        if (now.before(socialPractice.getStartTime()) || now.after(socialPractice.getEndTime())) {
            return CommonMethod.getReturnMessageError("当前不在报名时间范围内");
        }
        // 判断是否已经报名
        Optional<SocialPracticeSignup> ss = socialPracticeSignupRepository.findByStudentPersonIdAndSocialPracticePracticeId(student.getPersonId(),socialPracticeId);

        if (ss.isPresent()) {
            Boolean isSignedUp = ss.get().getStatus();
            if(isSignedUp) {
                // 学生报名过,且在报名中的状态
                return CommonMethod.getReturnMessageError("学生已报名该实践活动");
            }
            else if(!isSignedUp) {
                // 学生报名后取消报名了,记录还在,此时需要改成true
                ss.get().setStatus(true);
                ss.get().setSignupTime(new Date());

                socialPracticeSignupRepository.save(ss.get());
                return CommonMethod.getReturnMessageOK("报名成功");

            }
        }

        // 学生第一次报名

        SocialPracticeSignup signup = new SocialPracticeSignup();
        signup.setStudent(student);
        signup.setSocialPractice(SocialPracticeOpt.get());
        signup.setStatus(true);
        signup.setSignupTime(new Date());
        try {
            socialPracticeSignupRepository.save(signup);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError(e.getMessage());
        }

        return CommonMethod.getReturnMessageOK("报名成功");

    }

    public DataResponse cancelSignup(@Valid DataRequest dataRequest){
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        Integer personId = dataRequest.getInteger("personId");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }
        Optional<SocialPracticeSignup> signupOpt = socialPracticeSignupRepository.findByStudentPersonIdAndSocialPracticePracticeId(student.getPersonId(), socialPracticeId);
        if (signupOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("报名信息不存在");
        }



        SocialPracticeSignup socialPracticeSignup = signupOpt.get();
        if(socialPracticeSignup.getProofMaterialFileName() != null) {
            return CommonMethod.getReturnMessageError("已提交证明材料不能取消报名");

        }
        socialPracticeSignup.setStatus(false);
        socialPracticeSignupRepository.save(socialPracticeSignup);
        return CommonMethod.getReturnMessageOK("取消报名成功");
    }


    public DataResponse submitProofMaterial(@Valid DataRequest dataRequest) {
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        Integer studentId = dataRequest.getInteger("studentId");

        String proofMaterialFileName = dataRequest.getString("proofMaterialFileName");

        String proofMaterialFilePath = dataRequest.getString("proofMaterialFilePath");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(studentId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }
        Optional<SocialPracticeSignup> socialPracticeSignUpOpt = socialPracticeSignupRepository.findByStudentPersonIdAndSocialPracticePracticeId(student.getPersonId(), socialPracticeId);
        if (socialPracticeSignUpOpt.isPresent()) {
            SocialPracticeSignup socialPracticeSignup = socialPracticeSignUpOpt.get();

            if (socialPracticeSignup.getStatus() == false) {
                return  CommonMethod.getReturnMessageError("你未报名该社会实践");
            }
            socialPracticeSignup.setProofMaterialFileName(proofMaterialFileName);

            socialPracticeSignup.setProofMaterialFilePath(proofMaterialFilePath);

            socialPracticeSignupRepository.save(socialPracticeSignup);

            return CommonMethod.getReturnMessageOK("提交成功");

        }else {
            return  CommonMethod.getReturnMessageError("你未报名该社会实践");

        }
    }

    public DataResponse getSignupList(DataRequest dataRequest) {
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        if (socialPracticeId == null) {
            return CommonMethod.getReturnMessageError("缺少社会实践ID");
        }

        List<SocialPracticeSignup> signupList = socialPracticeSignupRepository.findBySocialPracticePracticeId(socialPracticeId);
        Map<String,Object> m;
        List<Map<String,Object>> dataList = new ArrayList<>();

        for (SocialPracticeSignup signup : signupList) {
            m = new HashMap<>();
            Student student = signup.getStudent();
            String num = student.getPerson().getNum();
            Date signupTime = signup.getSignupTime();

            m.put("num",num);
            m.put("signupTime",signupTime);

            m.put("name",student.getPerson().getName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

}
