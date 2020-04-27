package com.everchanging.hierarchy.repository;

import com.everchanging.hierarchy.dto.EmployeeSupervisors;
import com.everchanging.hierarchy.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    @Query("SELECT new com.everchanging.hierarchy.dto.EmployeeSupervisors(e.name, s.name, s.supervisor.name)" +
            " FROM EmployeeEntity e" +
            " LEFT JOIN EmployeeEntity s ON s.name = e.supervisor.name" +
            " WHERE UPPER(e.name) = UPPER(:name)")
    EmployeeSupervisors getSupervisors(@Param("name")String name);
}
