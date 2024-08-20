package com.mid.night.location;

public class LocationResponseDTO {

    public record GetLocationDTO(
            long longitude,
            long latitude
    ) {
    }
}
