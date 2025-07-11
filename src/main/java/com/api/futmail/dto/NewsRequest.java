package com.api.futmail.dto;

import com.api.futmail.model.NewsCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {
    
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 500, message = "Título deve ter no máximo 500 caracteres")
    private String title;
    
    @NotBlank(message = "Conteúdo é obrigatório")
    private String content;
    
    @Size(max = 1000, message = "Resumo deve ter no máximo 1000 caracteres")
    private String summary;
    
    @NotNull(message = "Categoria é obrigatória")
    private NewsCategory category;
    
    private String sourceUrl;
    private String sourceName;
    
    public boolean hasSource() {
        return sourceName != null && !sourceName.trim().isEmpty();
    }
    
    public boolean hasSourceUrl() {
        return sourceUrl != null && !sourceUrl.trim().isEmpty();
    }
    
    public boolean isComplete() {
        return title != null && !title.trim().isEmpty() &&
               content != null && !content.trim().isEmpty() &&
               category != null;
    }
}
