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
@Table(name = "tb_usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

  @Id
  @Column(name = "id_usuario", nullable = false)
  private Integer id;

  @Column(name = "nome_usuario", nullable = false, length = 100)
  private String nome;

  @Column(name = "email_usuario", nullable = false, length = 255)
  private String email;

  @Column(name = "senha_hash", nullable = false, length = 255)
  private String senhaHash;

  @Column (name = "registro_em", nullable = false, updatable = false)
  private OffsetDateTime registroEm;

  @Column(name = "ativo", nullable = false)
  private Boolean ativo;

  @Column(name = "endereco_id", nullable = false, insertable = false, updatable = false)
  private Integer enderecoId;;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tipo_usuario_id", nullable = false)
  private TipoUsuarioEntity tipoUsuario;

}
