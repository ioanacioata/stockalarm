package com.devmate.stockalarm.repository;

import com.devmate.stockalarm.model.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

  List<Alarm> findAllByIsActiveOrderByStockSymbol(Boolean isActive);
}
