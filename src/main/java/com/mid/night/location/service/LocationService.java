package com.mid.night.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LocationService {

    private static final long LONGITUDE = 37L;
    private static final long LATITUDE = 37L;


}
