package com.devmate.stockalarm.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"stockSymbol", "user_id"})
})
@Data
public class Alarm {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double initialPrice;

  private Double currentPrice;

  private Double variance;

  private Double target;

  private String stockSymbol;

  private Boolean isActive;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Alarm alarm = (Alarm) o;
    return Objects.equals(id, alarm.id) &&
        Objects.equals(initialPrice, alarm.initialPrice) &&
        Objects.equals(currentPrice, alarm.currentPrice) &&
        Objects.equals(variance, alarm.variance) &&
        Objects.equals(target, alarm.target) &&
        Objects.equals(stockSymbol, alarm.stockSymbol) &&
        Objects.equals(isActive, alarm.isActive);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, initialPrice, currentPrice, variance, target, stockSymbol, isActive);
  }

  @Override
  public String toString() {
    return "Alarm{" +
        "id=" + id +
        ", initialPrice=" + initialPrice +
        ", currentPrice=" + currentPrice +
        ", variance=" + variance +
        ", target=" + target +
        ", stockSymbol='" + stockSymbol + '\'' +
        ", isActive=" + isActive +
        '}';
  }
}
