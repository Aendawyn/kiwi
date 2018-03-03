package fr.aquillet.kiwi.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Label {

    @NonNull
    UUID id;
    @NonNull
    private String title;
    @NonNull
    private String hexColor;

}
