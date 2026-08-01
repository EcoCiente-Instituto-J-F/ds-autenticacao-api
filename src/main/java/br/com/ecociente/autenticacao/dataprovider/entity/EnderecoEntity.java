package br.com.ecociente.autenticacao.dataprovider.entity;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoEntity implements Persistable<Integer> {

  @Id
  @Column(name = "id_endereco", nullable = false)
  private Integer id;

  @Column(name = "cep", nullable = false)
  private String cep;

  @Column(name = "cidade", nullable = false)
  private String cidade;

  @Column(name = "estado", nullable = false)
  private String estado;

  @Column(name = "bairro")
  private String bairro;

  @Column(name = "rua", nullable = false)
  private String rua;

  @Column(name = "numero", nullable = false)
  private String numero;

  @Column(name = "complemento")
  private String complemento;

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
