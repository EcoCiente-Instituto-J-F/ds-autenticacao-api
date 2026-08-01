package br.com.ecociente.autenticacao.dataprovider.entity;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cooperativa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CooperativaEntity implements Persistable<Integer> {

  @Id
  @Column(name = "id_cooperativa", nullable = false)
  private Integer id;

  @Column(name = "cnpj", nullable = false)
  private String cnpj;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_usuario", nullable = false)
  private UsuarioEntity usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_endereco", nullable = false)
  private EnderecoEntity endereco;

  @Transient
  @Builder.Default
  private boolean novoRegistro = true;

  @Override
  public boolean isNew() {
    return novoRegistro;
  }

  @PostLoad
  void marcarComoExistente() {
    novoRegistro = false;
  }
}
