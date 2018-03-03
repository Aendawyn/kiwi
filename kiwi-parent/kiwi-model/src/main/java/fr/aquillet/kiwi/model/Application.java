package fr.aquillet.kiwi.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Application {

    @NonNull
    UUID id;
    @NonNull
    private String title;

}
