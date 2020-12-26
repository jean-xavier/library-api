package br.com.libraryapi.api.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnedLoanDTO {
    private Boolean returned;
}
