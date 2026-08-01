package br.com.ecociente.autenticacao.dataprovider.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sindico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SindicoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_sindico", nullable = false)
  private Integer id;

  @Column(name = "cpf")
  private String cpf;

  @Column(name = "data_inicio_mandato")
  private LocalDate dataInicioMandato;

  @Column(name = "data_fim_mandato")
  private LocalDate dataFimMandato;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_usuario", nullable = false)
  private UsuarioEntity usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_condominio", nullable = false)
  private CondominioEntity condominio;
}
