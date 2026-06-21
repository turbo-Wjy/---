package com.example.ailearning.module.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("enterprise_mentors")
public class EnterpriseMentor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long enterpriseId;
    private String position;
    private String contactPhoneEncrypted;
    private String contactPhoneIv;
    private String contactPhoneHash;
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
    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getContactPhoneEncrypted() { return contactPhoneEncrypted; }
    public void setContactPhoneEncrypted(String contactPhoneEncrypted) { this.contactPhoneEncrypted = contactPhoneEncrypted; }
    public String getContactPhoneIv() { return contactPhoneIv; }
    public void setContactPhoneIv(String contactPhoneIv) { this.contactPhoneIv = contactPhoneIv; }
    public String getContactPhoneHash() { return contactPhoneHash; }
    public void setContactPhoneHash(String contactPhoneHash) { this.contactPhoneHash = contactPhoneHash; }
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
