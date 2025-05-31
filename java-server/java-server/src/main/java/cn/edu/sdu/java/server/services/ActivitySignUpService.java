package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.ActivitySignup;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.ActivitySignupRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ActivitySignUpService {

    @Autowired
    private ActivitySignupRepository activitySignupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ActivityRepository activityRepository;

    // 学生报名活动
    @Transactional
    public DataResponse newSignUp(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer activityId = dataRequest.getInteger("activityId");
        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }else
        {
            return CommonMethod.getReturnMessageError("请使用学生账号报名");
        }


        Optional<Activity> activityOpt = activityRepository.findById(activityId);
        if (activityOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("活动不存在");
        }

        // 判断是否已经报名
        ActivitySignup as = activitySignupRepository.findByActivityActivityId(activityId);

        if (as!=null) {
            Boolean isSignedUp = as.getStatus();
            if(isSignedUp) {
                // 学生报名过,且在报名中的状态
                return CommonMethod.getReturnMessageError("学生已报名该活动");
            }
            else if(!isSignedUp) {
                // 学生报名后取消报名了,记录还在,此时需要改成true
                as.setStatus(true);
                as.setSignupTime(new Date());

                activitySignupRepository.save(as);
                return CommonMethod.getReturnMessageOK("报名成功");

            }
        }

        // 学生第一次报名
        ActivitySignup signup = new ActivitySignup();
        signup.setStudent(student);
        signup.setActivity(activityOpt.get());
        signup.setStatus(true);
        signup.setSignupTime(new Date());
        try {
            activitySignupRepository.save(signup);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError(e.getMessage());
        }

        return CommonMethod.getReturnMessageOK("报名成功");
    }

    // 取消报名
    public DataResponse cancelSignUp(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer activityId = dataRequest.getInteger("activityId");

        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }
        Optional<ActivitySignup> signupOpt = activitySignupRepository.findByStudentPersonIdAndActivityActivityId(student.getPersonId(), activityId);
        if (signupOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("报名信息不存在");
        }
        ActivitySignup activitySignup = signupOpt.get();
        activitySignup.setStatus(false);
        activitySignupRepository.save(activitySignup);
        return CommonMethod.getReturnMessageOK("取消报名成功");
    }

//    // 获取某活动的报名学生列表
//    public DataResponse getSignupList(@Valid DataRequest dataRequest) {
//        Integer activityId = dataRequest.getInteger("activityId");
//
//        List<ActivitySignup> signups = activitySignupRepository.findByActivityActivityId(activityId);
//        List<Map<String, Object>> result = new ArrayList<>();
//        for (ActivitySignup signup : signups) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("studentNum", signup.getStudent().getPerson().getNum());
//            map.put("name", signup.getStudent().getPerson().getName());
//            map.put("signupId", signup.getSignupId());
//            result.add(map);
//        }
//        return CommonMethod.getReturnData(result);
//    }
}
