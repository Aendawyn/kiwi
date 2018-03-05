package fr.aquillet.kiwi.model;

import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Campaign {

    @NonNull
    UUID id;
    @NonNull
    private String title;
    @NonNull
    private Optional<UUID> labelId;
    @NonNull
    private List<UUID> scenarioIds;
}
