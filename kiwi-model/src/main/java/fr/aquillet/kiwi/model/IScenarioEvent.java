package fr.aquillet.kiwi.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ //
        @JsonSubTypes.Type(value = ScenarioNativeEvent.class) //
})
public interface IScenarioEvent {

    long getTime();

    void setTime(long time);

}
