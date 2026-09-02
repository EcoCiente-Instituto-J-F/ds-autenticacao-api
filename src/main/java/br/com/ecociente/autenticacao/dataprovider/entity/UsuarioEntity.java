package br.com.ecociente.autenticacao.dataprovider.entity;

import java.time.OffsetDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

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

  @Column (name = "id_endereco", nullable = false, insertable = false, updatable = false)
  private Integer endereco;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_tipo_usuario", nullable = false)
  private TipoUsuarioEntity tipoUsuario;

}
