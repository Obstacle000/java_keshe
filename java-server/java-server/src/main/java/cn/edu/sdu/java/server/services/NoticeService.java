package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.repositorys.NoticeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private ActivityRepository activityRepository;

    // 获取通知列表
    public DataResponse getNoticeList(@Valid DataRequest dataRequest) {
        List<Notice> notices = noticeRepository.findAll();
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Notice notice : notices) {
            Map<String, Object> m = new HashMap<>();
            m.put("noticeId", notice.getNoticeId());
            m.put("title", notice.getTitle());
            m.put("content", notice.getContent());
            m.put("createTime", notice.getCreateTime());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 获取某个通知详情
    public DataResponse getNoticeContent(@Valid DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("通知不存在");
        }
        Notice notice = noticeOptional.get();
        Map<String, Object> m = new HashMap<>();
        m.put("noticeId", notice.getNoticeId());
        m.put("title", notice.getTitle());
        m.put("content", notice.getContent());
        m.put("createTime", notice.getCreateTime());
        return CommonMethod.getReturnData(m);
    }

    // 添加通知
    public DataResponse addNotice(@Valid DataRequest dataRequest) {
        Notice notice = new Notice();
        notice.setTitle(dataRequest.getString("title"));
        notice.setContent(dataRequest.getString("content"));
        notice.setCreateTime(new Date()); // 当前时间
        noticeRepository.save(notice);
        return CommonMethod.getReturnMessageOK("添加成功");
    }

    // 更新通知
    public DataResponse updateNotice(@Valid DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (noticeOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("通知不存在");
        }
        Notice notice = noticeOptional.get();
        notice.setTitle(dataRequest.getString("title"));
        notice.setContent(dataRequest.getString("content"));
        noticeRepository.save(notice);
        return CommonMethod.getReturnMessageOK("更新成功");
    }

    // 删除通知
    public DataResponse deleteNotice(@Valid DataRequest dataRequest) {
        Integer noticeId = dataRequest.getInteger("noticeId");
        if (!noticeRepository.existsById(noticeId)) {
            return CommonMethod.getReturnMessageError("通知不存在");
        }

        // 判断是否有活动关联此通知
        List<Activity> activities = activityRepository.findByNoticeNoticeId(noticeId);
        if (activities != null && !activities.isEmpty()) {
            return CommonMethod.getReturnMessageError("该通知已被活动关联，无法删除");
        }

        // 没有关联才删除
        noticeRepository.deleteById(noticeId);
        return CommonMethod.getReturnMessageOK("删除成功");
    }

}
