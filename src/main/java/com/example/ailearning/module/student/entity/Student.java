package com.example.ailearning.module.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("students")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String studentNo;
    private Long collegeId;
    private Long majorId;
    private Long classId;
    private String grade;
    private String gender;
    private String enrollmentStatus;
    private String phoneEncrypted;
    private String phoneIv;
    private String phoneHash;
    private String emailEncrypted;
    private String emailIv;
    private String emailHash;
    private String idCardNoEncrypted;
    private String idCardNoIv;
    private String idCardNoHash;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public Long getMajorId() {
        return majorId;
    }

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public String getPhoneEncrypted() {
        return phoneEncrypted;
    }

    public void setPhoneEncrypted(String phoneEncrypted) {
        this.phoneEncrypted = phoneEncrypted;
    }

    public String getPhoneIv() {
        return phoneIv;
    }

    public void setPhoneIv(String phoneIv) {
        this.phoneIv = phoneIv;
    }

    public String getPhoneHash() {
        return phoneHash;
    }

    public void setPhoneHash(String phoneHash) {
        this.phoneHash = phoneHash;
    }

    public String getEmailEncrypted() {
        return emailEncrypted;
    }

    public void setEmailEncrypted(String emailEncrypted) {
        this.emailEncrypted = emailEncrypted;
    }

    public String getEmailIv() {
        return emailIv;
    }

    public void setEmailIv(String emailIv) {
        this.emailIv = emailIv;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getIdCardNoEncrypted() {
        return idCardNoEncrypted;
    }

    public void setIdCardNoEncrypted(String idCardNoEncrypted) {
        this.idCardNoEncrypted = idCardNoEncrypted;
    }

    public String getIdCardNoIv() {
        return idCardNoIv;
    }

    public void setIdCardNoIv(String idCardNoIv) {
        this.idCardNoIv = idCardNoIv;
    }

    public String getIdCardNoHash() {
        return idCardNoHash;
    }

    public void setIdCardNoHash(String idCardNoHash) {
        this.idCardNoHash = idCardNoHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
