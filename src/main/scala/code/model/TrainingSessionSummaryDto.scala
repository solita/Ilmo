package code.model
import java.util.Date

class TrainingSessionSummaryDto(name: String, date: Date) {
  def name(): String = name
  def date(): Date = date
}