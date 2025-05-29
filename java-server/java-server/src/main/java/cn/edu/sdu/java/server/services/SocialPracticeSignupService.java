package cn.edu.sdu.java.server.services;


import cn.edu.sdu.java.server.models.SocialPractice;
import cn.edu.sdu.java.server.models.SocialPracticeSignup;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.SocialPracticeRepository;
import cn.edu.sdu.java.server.repositorys.SocialPracticeSignupRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class SocialPracticeSignupService {

    @Autowired
    private SocialPracticeRepository socialPracticeRepository;

    @Autowired
    private SocialPracticeSignupRepository socialPracticeSignupRepository;

    @Autowired
    private StudentRepository studentRepository;


    public DataResponse signup(@Valid DataRequest dataRequest){
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        SocialPractice socialPractice=socialPracticeSignupRepository.findBySocialPracticePracticeId(socialPracticeId).getSocialPractice();

        Integer studentId = dataRequest.getInteger("studentId");

        Optional<Student> student = studentRepository.findByPersonPersonId(studentId);

        if(socialPracticeSignupRepository.existsByStudentPersonIdAndSocialPracticePracticeId(studentId,socialPracticeId)){
            return CommonMethod.getReturnMessageError("你已参加该社会实践");
        }

        SocialPracticeSignup socialPracticeSignup = new SocialPracticeSignup();
        socialPracticeSignup.setSocialPractice(socialPractice);
        socialPracticeSignup.setStudent(student.get());
        socialPracticeSignup.setSignupTime(new Date());
        socialPracticeSignup.setStatus(true);

        socialPracticeSignupRepository.save(socialPracticeSignup);

        return CommonMethod.getReturnMessageOK("报名成功");

    }

    public DataResponse cancelSignup(@Valid DataRequest dataRequest){
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        Integer studentId = dataRequest.getInteger("studentId");

        Optional<SocialPracticeSignup> socialPracticeSignupOpt = socialPracticeSignupRepository.findByStudentPersonIdAndSocialPracticePracticeId(studentId,socialPracticeId);
        if(socialPracticeSignupOpt.isPresent()){
            socialPracticeSignupRepository.delete(socialPracticeSignupOpt.get());
            return  CommonMethod.getReturnMessageOK("取消成功");
        }
        return CommonMethod.getReturnMessageError("你未报名该社会实践");
    }


    public DataResponse submitProofMaterial(@Valid DataRequest dataRequest) {
        Integer socialPracticeId = dataRequest.getInteger("socialPracticeId");

        Integer studentId = dataRequest.getInteger("studentId");

        String proofMaterialFileName = dataRequest.getString("proofMaterialFileName");

        String proofMaterialFilePath = dataRequest.getString("proofMaterialFilePath");

        Optional<SocialPracticeSignup> socialPracticeSignUpOpt = socialPracticeSignupRepository.findByStudentPersonIdAndSocialPracticePracticeId(studentId, socialPracticeId);

        if (socialPracticeSignUpOpt.isPresent()) {
            SocialPracticeSignup socialPracticeSignup = socialPracticeSignUpOpt.get();

            socialPracticeSignup.setProofMaterialFileName(proofMaterialFileName);

            socialPracticeSignup.setProofMaterialFilePath(proofMaterialFilePath);

            socialPracticeSignupRepository.save(socialPracticeSignup);

            return CommonMethod.getReturnMessageOK("提交成功");
        }
        return  CommonMethod.getReturnMessageError("你未报名该社会实践");
    }
}
