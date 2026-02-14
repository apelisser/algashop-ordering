package com.apelisser.algashop.ordering.infrastructure.utility.modelmapper;

import com.apelisser.algashop.ordering.application.customer.query.CustomerOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderDetailOutput;
import com.apelisser.algashop.ordering.application.order.query.OrderItemDetailOutput;
import com.apelisser.algashop.ordering.application.utility.Mapper;
import com.apelisser.algashop.ordering.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.domain.model.customer.BirthDate;
import com.apelisser.algashop.ordering.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import io.hypersistence.tsid.TSID;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ModelMapperConfig {

    private static final Converter<FullName, String> fullNameToFirstNameConverter;
    private static final Converter<FullName, String> fullNameToLastNameConverter;
    private static final Converter<BirthDate, LocalDate> birthDateToLocalDateConverter;
    private static final Converter<Long, String> longToStringTSIDConverter;

    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        this.configuration(modelMapper);
        return modelMapper::map;
    }

    private void configuration(ModelMapper modelMapper) {
        modelMapper.getConfiguration()
            .setSourceNamingConvention(NamingConventions.NONE)
            .setDestinationNamingConvention(NamingConventions.NONE)
            .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Customer.class, CustomerOutput.class)
            .addMappings(mapping -> mapping.using(fullNameToFirstNameConverter).map(Customer::fullName, CustomerOutput::setFirstName))
            .addMappings(mapping -> mapping.using(fullNameToLastNameConverter).map(Customer::fullName, CustomerOutput::setLastName))
            .addMappings(mapping -> mapping.using(birthDateToLocalDateConverter).map(Customer::birthDate, CustomerOutput::setBirthDate));

        modelMapper.createTypeMap(OrderPersistenceEntity.class, OrderDetailOutput.class)
            .addMappings(mapping -> mapping.using(longToStringTSIDConverter).map(OrderPersistenceEntity::getId, OrderDetailOutput::setId));

        modelMapper.createTypeMap(OrderItemPersistenceEntity.class, OrderItemDetailOutput.class)
            .addMappings(mapping -> mapping.using(longToStringTSIDConverter).map(OrderItemPersistenceEntity::getId, OrderItemDetailOutput::setId))
            .addMappings(mapping -> mapping.using(longToStringTSIDConverter).map(OrderItemPersistenceEntity::getOrderId, OrderItemDetailOutput::setOrderId));
    }

    static {
        fullNameToFirstNameConverter = mappingContext -> {
            FullName fullName = mappingContext.getSource();
            return fullName != null ? fullName.firstName() : null;
        };

        fullNameToLastNameConverter = mappingContext -> {
            FullName fullName = mappingContext.getSource();
            return fullName != null ? fullName.lastName() : null;
        };

        birthDateToLocalDateConverter = mappingContext -> {
            BirthDate birthDate = mappingContext.getSource();
            return birthDate != null ? birthDate.value() : null;
        };

        longToStringTSIDConverter = mappingContext -> {
            Long tsidAsLong = mappingContext.getSource();
            return tsidAsLong != null ? new TSID(tsidAsLong).toString() : null;
        };
    }

}
