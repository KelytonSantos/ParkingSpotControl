package com.api.parkingcontrol.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
// import java.util.List;
import java.util.Optional;
// import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600) //Permitir que seja acessado dee qualquer fonte
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    
    @Autowired
    public ParkingSpotService parkingSpotService;

    @PostMapping // método post/*responseEntity para montar uma resposta tanto com status como o corpo dessa resposta*/
    /*O object é porque teremos outros tipos de retorno*/
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){ //pegando na requisição um parkingspotDto
    /*Nessas linhas abaixo eu estou fazendo com que antes que eu salve aqueles dados ele vai ver se ja existe uma placa de carro igual a descrita, uma vaga ja registrada, e um apartamento ou bloco ja cadastrado */
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate car is already in use");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use");
        }
        if(parkingSpotService.existsByApartmentAndBLock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }

        var parkingSpotModel = new ParkingSpotModel();
        //o var serve pra indicar que é do tipo ParkingSpotModel
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel); //convertendo o dto em model(uma das maneiras de converter é utilizando o beanUtil.copyproperties)
        //primeiro passamos o que vai ser convertido e depois no que vai ser convertido

        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));//setando a data de registro com localDateTime e identificando o formato utc
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel)); /*Aqui eu construo a resposta usando o entyty status
        onde eu passo o status criado e no body eu passo o retorno do método save:que vai ser a vaga de estacionamento salva com os dados*/
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") Long id){
        System.out.println(id);
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable (value = "id") Long id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking not found.");
        }
        parkingSpotService.delete(parkingSpotModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot Deleted sucessfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") Long id, @RequestBody @Valid ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if(!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
        }
        var parkingSpotModel = new ParkingSpotModel(); //Criando uma instância da minha tabela
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);//o que chega em dto, eu converto para model(sera possivel inserir na tabela)
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId()); /*pegando o id no findById e salvando na minha tabela(sobrescrita com o mesmo dado) isso vai indicar que voce
        esta querendo alterar algum dado correspondente a esse id(sem essa linha, o compilador da erro achando que voce ta querendo criar um novo campo com mesmos dados ja inseridos em outro campo)*/
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());//Nessa linha to pegando e setando a mesma data(sem ela o compilador acusa erro dizendo que faltou uma data(not null))
        //Esses dois sets são para dizer o que se mantem e que nunca mudam. 

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));/*todos os dados que vieram no dto que estão diferentes do escrito na tabela, 
        foram convertidos em model. nessa ultima linha eu salvo o meu spotmodel com dados ja convertidos. */
    }
}