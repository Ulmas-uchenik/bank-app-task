package org.example.lesson1First.entity.dto.mapper;

import org.example.lesson1First.entity.db.Transaction;
import org.example.lesson1First.entity.dto.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mappings({
            @Mapping(target = "sourceId", expression = "java(transaction.getSourceBankAccountNumber())"),
            @Mapping(target = "targetId", expression = "java(transaction.getTargetBankAccountNumber())")
    })
    TransactionResponse toResponse(Transaction transaction);
}
