package vn.thuephonghoc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@NoArgsConstructor
@Table(name = "gen_config")
public class GenConfig {

    public GenConfig(String tableName) {
        this.cover = false;
        this.tableName = tableName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String apiAlias;

    @NotBlank
    private String pack;

    @NotBlank
    private String path;

    @Column(name = "api_path")
    private String apiPath;

    @NotBlank
    private String tableName;

    private String author;

    private String prefix;

    private Boolean cover;
}
