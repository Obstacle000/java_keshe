package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.repositorys.NoticeRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.StudentSignUpRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ActivityService {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    StudentSignUpRepository studentSignUpRepository;
    @Autowired
    NoticeRepository noticeRepository;

    public DataResponse getActivityList(@Valid DataRequest dataRequest) {
        List<Activity> lists = activityRepository.findAll();
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Activity activity : lists) {
            // 根据活动去参加表里获取人数
            m = new HashMap<>();
            long count = studentSignUpRepository.countByActivityId(activity.getActivityId());
            m.put("signupCount",count);
            m.put("activityId",activity.getActivityId());
            m.put("title",activity.getTitle());
            m.put("description",activity.getDescription());
            m.put("startTime",activity.getStartTime());
            m.put("endTime",activity.getEndTime());
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
    public DataResponse getActivityContent(@Valid DataRequest dataRequest) {
        // 根据活动id去通知表里找content
        Integer activityId = dataRequest.getInteger("activityId");
        Optional<Activity> byId = activityRepository.findById(activityId);
        Activity activity= null;
        if (byId.isPresent()) {
            activity = byId.get();
        }
        Notice notice = activity.getNotice();
        if (notice == null) {
            return CommonMethod.getReturnMessageError("该活动没有关联通知");
        }
        dataRequest.add("content",notice.getContent());
        return CommonMethod.getReturnData(dataRequest);
    }

    public DataResponse addActivity(@Valid DataRequest dataRequest) {
        String title = dataRequest.getString("title");
        String description = dataRequest.getString("description");
        Date startTime = dataRequest.getDate("startTime");
        Date endTime = dataRequest.getDate("endTime");
        Integer noticeId = dataRequest.getInteger("noticeId");

        Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
        if (noticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("通知不存在");
        }

        Activity activity = new Activity();
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setNotice(noticeOpt.get());

        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK("添加成功");
    }


    public DataResponse deleteActivity(@Valid DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Optional<Activity> activityOpt = activityRepository.findById(activityId);
        Activity activity = null;

        if (activityOpt.isPresent()) {
            activity = activityOpt.get();
        }

        // 先删除报名记录（如果有的话）
        studentSignUpRepository.deleteByActivity(activity);

        activityRepository.deleteById(activityId);
        return CommonMethod.getReturnMessageOK("删除成功");
    }


    public DataResponse updateActivity(@Valid DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Optional<Activity> activityOpt = activityRepository.findById(activityId);

        if (activityOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("活动不存在");
        }

        Activity activity = activityOpt.get();

        activity.setTitle(dataRequest.getString("title"));
        activity.setDescription(dataRequest.getString("description"));
        activity.setStartTime(dataRequest.getDate("startTime"));
        activity.setEndTime(dataRequest.getDate("endTime"));

        Integer noticeId = dataRequest.getInteger("noticeId");
        Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
        if (noticeOpt.isEmpty()) {
            return CommonMethod.getReturnMessageError("关联通知不存在");
        }

        activity.setNotice(noticeOpt.get());

        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK("更新成功");
    }



}
