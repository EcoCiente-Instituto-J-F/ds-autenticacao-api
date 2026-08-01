package br.com.ecociente.autenticacao.dataprovider.entity;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity implements Persistable<Integer> {

  @Id
  @Column(name = "id_usuario", nullable = false)
  private Integer id;

  @Column(name = "nome", nullable = false)
  private String nome;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "senha_hash", nullable = false)
  private String senhaHash;

  @Column(name = "data_cadastro")
  private OffsetDateTime dataCadastro;

  @Column(name = "status")
  private Boolean status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_endereco", nullable = false)
  private EnderecoEntity endereco;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_tipo_usuario", nullable = false)
  private TipoUsuarioEntity tipoUsuario;

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

  @PrePersist
  void prePersist() {
    if (status == null) {
      status = true;
    }
    if (dataCadastro == null) {
      dataCadastro = OffsetDateTime.now();
    }
  }
}
