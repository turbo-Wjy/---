package com.example.ailearning.module.teacher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("teachers")
public class Teacher {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String teacherNo;
    private Long collegeId;
    private String title;
    private String phoneEncrypted;
    private String phoneIv;
    private String phoneHash;
    private String emailEncrypted;
    private String emailIv;
    private String emailHash;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTeacherNo() { return teacherNo; }
    public void setTeacherNo(String teacherNo) { this.teacherNo = teacherNo; }
    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPhoneEncrypted() { return phoneEncrypted; }
    public void setPhoneEncrypted(String phoneEncrypted) { this.phoneEncrypted = phoneEncrypted; }
    public String getPhoneIv() { return phoneIv; }
    public void setPhoneIv(String phoneIv) { this.phoneIv = phoneIv; }
    public String getPhoneHash() { return phoneHash; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
    public String getEmailEncrypted() { return emailEncrypted; }
    public void setEmailEncrypted(String emailEncrypted) { this.emailEncrypted = emailEncrypted; }
    public String getEmailIv() { return emailIv; }
    public void setEmailIv(String emailIv) { this.emailIv = emailIv; }
    public String getEmailHash() { return emailHash; }
    public void setEmailHash(String emailHash) { this.emailHash = emailHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
