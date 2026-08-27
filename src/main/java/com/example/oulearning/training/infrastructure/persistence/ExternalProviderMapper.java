package com.example.oulearning.training.infrastructure.persistence;

import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
interface ExternalProviderMapper {

    @Select("SELECT id, name, email, phone, active FROM external_provider WHERE id = #{id}")
    Optional<ExternalProviderEntity> findById(Long id);

    @Insert("INSERT INTO external_provider (name, email, phone, active) VALUES (#{name}, #{email}, #{phone}, #{active})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(ExternalProviderEntity entity);

    @Update("UPDATE external_provider SET name = #{name}, email = #{email}, phone = #{phone}, active = #{active} WHERE id = #{id}")
    void update(ExternalProviderEntity entity);
}
