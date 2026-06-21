package com.example.ailearning.module.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("enterprises")
public class Enterprise {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String industry;
    private String contactName;
    private String contactPhoneEncrypted;
    private String contactPhoneIv;
    private String contactPhoneHash;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhoneEncrypted() { return contactPhoneEncrypted; }
    public void setContactPhoneEncrypted(String contactPhoneEncrypted) { this.contactPhoneEncrypted = contactPhoneEncrypted; }
    public String getContactPhoneIv() { return contactPhoneIv; }
    public void setContactPhoneIv(String contactPhoneIv) { this.contactPhoneIv = contactPhoneIv; }
    public String getContactPhoneHash() { return contactPhoneHash; }
    public void setContactPhoneHash(String contactPhoneHash) { this.contactPhoneHash = contactPhoneHash; }
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
