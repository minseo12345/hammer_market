package com.hammer.hammer.category.entity;
import jakarta.persistence.*;
import lombok. *;
import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id", nullable = false)
    private Long categoryId;
	
	@Column(name = "name", nullable = false)
    private String name;
	
	@Column(name = "description")
    private String description;
	
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;

   

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}