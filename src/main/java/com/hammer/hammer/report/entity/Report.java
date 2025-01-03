package com.hammer.hammer.report.entity;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId; 

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", nullable = false)
    private Item item; 
    
    @ManyToOne
    @JoinColumn(name = "reporter_email", referencedColumnName = "email", nullable = false)
    private User reporter; // Foreign key to User table (email)

    @ManyToOne
    @JoinColumn(name = "reported_email", referencedColumnName = "email", nullable = false)
    private User reported;

    @Column(name = "report_cnt", nullable = false)
    private Integer reportCount; 

    @Column(name = "report_content", length = 100)
    private String reportContent; 
}