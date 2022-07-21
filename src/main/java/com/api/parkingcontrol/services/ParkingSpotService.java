package com.api.parkingcontrol.services;

// import java.util.List;
import java.util.Optional;
// import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;

@Service
public class ParkingSpotService {
    
    @Autowired/*Estou dizendo para o spring que em determinados momentos ele vai ter que injetar uma dependencia de parkingspotrepositorie aqui dentro de parkingSpotService*/
    public ParkingSpotRepository parkingSpotRepository;//Ponto de injeção(isso é pra substituir o famoso construtor)

    @Transactional//Quando for métodos construtivos ou destrutivos é interessante usar este por conta que caso dê algum erro ele garante um go back(n tendo dados quebrados)
    public ParkingSpotModel save(ParkingSpotModel parkingSpotModel) {
        return parkingSpotRepository.save(parkingSpotModel); /*to usando um atributo do repositorie(que acessa meu banco)
        para salvar um parkingSpotModel*/
    }

    public boolean existsByLicensePlateCar(String licensePlateCar) {
        return parkingSpotRepository.existsByLicensePlateCar(licensePlateCar);
    }

    public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
        return parkingSpotRepository.existsByParkingSpotNumber(parkingSpotNumber);
    }

    public boolean existsByApartmentAndBLock(String apartment, String block) {
        return parkingSpotRepository.existsByApartmentAndBlock(apartment, block);
    }

    public Page<ParkingSpotModel> findAll(Pageable pageable) {
        return parkingSpotRepository.findAll(pageable);
    }

    public Optional<ParkingSpotModel> findById(Long id) {
        return parkingSpotRepository.findById(id);
    }

    @Transactional
    public void delete(ParkingSpotModel parkingSpotModel){
        parkingSpotRepository.delete(parkingSpotModel);
    }

}