package br.com.ecociente.autenticacao.dataprovider.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "condominio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CondominioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_condominio", nullable = false)
  private Integer id;

  @Column(name = "nome", nullable = false)
  private String nome;

  @Column(name = "cnpj")
  private String cnpj;

  @Column(name = "status")
  private Boolean status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_endereco", nullable = false)
  private EnderecoEntity endereco;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_tipo_condominio")
  private TipoCondominioEntity tipoCondominio;
}
