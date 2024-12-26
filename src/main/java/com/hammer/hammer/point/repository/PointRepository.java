package com.hammer.hammer.point.repository;

import com.hammer.hammer.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<List<Point>> findByUser_UserId(long userId);
}
