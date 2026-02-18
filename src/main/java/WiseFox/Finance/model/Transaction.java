package WiseFox.Finance.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Double amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TransactionType type; // INCOME, EXPENSE

	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(nullable = false)
	private LocalDate date;

	@Column(columnDefinition = "MEDIUMTEXT")
	private String note;

	@ManyToOne
	@JoinColumn(name = "ledger_id", nullable = false)
	private Ledger ledger;

	public enum TransactionType {
		INCOME, EXPENSE
	}

	public enum Category {
		FOOD, TRANSPORT, RENT, ENTERTAINMENT, HEALTH, SHOPPING, SALARY, OTHER
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Ledger getLedger() {
		return ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}
}
