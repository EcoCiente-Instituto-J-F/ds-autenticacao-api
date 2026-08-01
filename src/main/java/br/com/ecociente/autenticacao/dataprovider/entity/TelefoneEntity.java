package br.com.ecociente.autenticacao.dataprovider.entity;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "telefone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefoneEntity implements Persistable<Integer> {

  @Id
  @Column(name = "id_telefone", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_usuario")
  private UsuarioEntity usuario;

  @Column(name = "numero", nullable = false)
  private String numero;

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
