package org.imperial.fastquantanalysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@TableName("users")
public class User implements Serializable {

    private static final long serialVersionUID = 5155736403725997140L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField(value = "email_id")
    private String emailId;

    @TableField(value = "password")
    private String password;

    @TableField(value = "first_name")
    private String firstName;

    @TableField(value = "last_name")
    private String lastName;

    @TableField(value = "date_of_birth")
    private LocalDate dateOfBirth;

    @TableField(value = "created_at", fill = FieldFill.INSERT) // Autofill when inserting
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) // Autofill when inserting and updating
    private LocalDateTime updatedAt;

    @TableField(exist = false) // Not mapped to user table
    private Set<CompletedTicket> completedTickets;

    @TableField(exist = false)
    private Set<FutureTicket> futureTickets;

    @TableField(exist = false)
    private Set<Goal> goals;

    @TableField(exist = false)
    private Set<Note> notes;

    @TableField(exist = false)
    private Set<CurrentMonthlySpendingThresholdLimit> currentMonthlySpendingThresholdLimits;

    @TableField(exist = false)
    private Set<SpendingThresholdRecord> spendingThresholdRecords;
}
