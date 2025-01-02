package com.hammer.hammer.point.repository;

import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.point.entity.PointStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
   Page<Point> findByUser_UserIdOrderByCreateDateDesc(Long userId, Pageable pageable);
   Page<Point> findByUser_UserIdAndPointTypeOrderByCreateDateDesc(Long userId, PointStatus pointStatus,Pageable pageable);
}
