package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.SocialPractice;
import cn.edu.sdu.java.server.models.SocialPracticeSignup;
import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.SocialPracticeRepository;
import cn.edu.sdu.java.server.repositorys.NoticeRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.StudentSocialPracticeSignUpRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SocialPracticeService {
    @Autowired
    SocialPracticeRepository socialPracticeRepository;
    @Autowired
    StudentSocialPracticeSignUpRepository studentSocialPracticeSignUpRepository;
    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    StudentRepository studentRepository;

    public DataResponse getSocialPracticeList(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student student = null;
        if (byPersonPersonId.isPresent()) {
            student = byPersonPersonId.get();
        }

        List<SocialPractice> lists = socialPracticeRepository.findAll();
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (SocialPractice socialPractice : lists) {
            m = new HashMap<>();
            long count = studentSocialPracticeSignUpRepository.countBySocialPracticeId(socialPractice.getPracticeId());

            // 默认未报名
            m.put("isSignedUp", false);

            // 如果是学生，则去查询报名状态
            if (student != null) {
                SocialPracticeSignup as = studentSocialPracticeSignUpRepository.findByStudentAndSocialPractice(student, socialPractice);
                if (as != null && Boolean.TRUE.equals(as.getStatus())) {
                    m.put("isSignedUp", true);
                }
            }

            m.put("signupCount", count);
            m.put("socialPracticeId", socialPractice.getPracticeId());
            m.put("title", socialPractice.getTitle());
            m.put("description", socialPractice.getDescription());
            m.put("startTime", socialPractice.getStartTime() == null ? "" : sdf.format(socialPractice.getStartTime()));
            m.put("endTime", socialPractice.getEndTime() == null ? "" : sdf.format(socialPractice.getEndTime()));

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
    public DataResponse getSocialPracticeContent(@Valid DataRequest dataRequest) {
        // 根据社会实践活动id去通知表里找content
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");
        Optional<SocialPractice> byId = socialPracticeRepository.findById(socialPracticeId);
        SocialPractice socialPractice= null;
        if (byId.isPresent()) {
            socialPractice = byId.get();
        }
        Notice notice = socialPractice.getNotice();
        if (notice == null) {
            return CommonMethod.getReturnMessageError("该社会实践没有关联通知");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("noticeId", notice.getNoticeId());
        m.put("title", notice.getTitle());
        m.put("content", notice.getContent());
        m.put("createTime", notice.getCreateTime());
        return CommonMethod.getReturnData(m);
    }
    @Transactional
    public DataResponse addSocialPractice(@Valid DataRequest dataRequest) {
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



        SocialPractice socialPractice = new SocialPractice();
        socialPractice.setTitle(title);
        socialPractice.setDescription(description);
        socialPractice.setStartTime(startTime);
        socialPractice.setEndTime(endTime);
        socialPractice.setNotice(noticeOpt.get());

        socialPracticeRepository.save(socialPractice);


        return CommonMethod.getReturnMessageOK("添加成功");
    }


    @Transactional
    public DataResponse deleteSocialPractice(@Valid DataRequest dataRequest) {
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");
        Optional<SocialPractice> socialPracticeOpt = socialPracticeRepository.findById(socialPracticeId);
        SocialPractice socialPractice = null;

        if (socialPracticeOpt.isPresent()) {
            socialPractice = socialPracticeOpt.get();
        }

        // 先删除报名记录（如果有的话）
        studentSocialPracticeSignUpRepository.deleteBySocialPractice(socialPractice);

        socialPracticeRepository.deleteById(socialPracticeId);
        return CommonMethod.getReturnMessageOK("删除成功");
    }


    public DataResponse updateSocialPractice(@Valid DataRequest dataRequest) {
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");
        Optional<SocialPractice> socialPracticeOpt = socialPracticeRepository.findById(socialPracticeId);

        if (socialPracticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("社会实践不存在");
        }

        SocialPractice socialPractice = socialPracticeOpt.get();

        socialPractice.setTitle(dataRequest.getString("title"));
        socialPractice.setDescription(dataRequest.getString("description"));
        socialPractice.setStartTime(dataRequest.getDate("startTime"));
        socialPractice.setEndTime(dataRequest.getDate("endTime"));

        Integer noticeId = dataRequest.getInteger("noticeId");
        Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
        if (noticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("关联通知不存在");
        }

        socialPractice.setNotice(noticeOpt.get());

        socialPracticeRepository.save(socialPractice);
        return CommonMethod.getReturnMessageOK("更新成功");
    }


    public SocialPractice findById(Integer id) {
        return socialPracticeRepository.findById(id).orElse(null);
    }

    public void save(SocialPractice socialPractice) {
        socialPracticeRepository.save(socialPractice);
    }



}
