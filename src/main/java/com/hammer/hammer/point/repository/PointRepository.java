package com.hammer.hammer.point.repository;

import com.hammer.hammer.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
