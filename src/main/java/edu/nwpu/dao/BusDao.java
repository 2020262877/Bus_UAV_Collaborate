package edu.nwpu.dao;

import edu.nwpu.domain.Bus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.LinkedList;
import java.util.List;

public interface BusDao {

    @Insert("insert into bus1(PlateId,Date,Latitude,Longitude,Speed) values(#{PlateId},#{Date},#{Latitude},#{Longitude},#{Speed})")
    int insertBus(Bus bus);

    @Update("update bus1 set Speed=#{Speed} where PlateId=#{PlateId} and Date=#{Date}")
    void updateSpeed(Bus bus);

    @Select("select PlateId,Date,Latitude,Longitude,Speed from bus1 limit #{rowNumber},1")
    Bus selectByRow(int rowNumber);

    @Select("select PlateId,Date,Latitude,Longitude,Speed from bus1 limit #{startRowNumber},#{endRowNumber}")
    List<Bus> selectByRowContent(int startRowNumber,int endRowNumber);

    @Select("select PlateId,Date,Latitude,Longitude,Speed from bus1 where PlateId=#{PlateId}")
    LinkedList<Bus> selectByPlateId(String PlateId);

    @Select("select count(*) from bus1")
    Integer getCount();

    @Select("select RowNumber from bus1 where PlateId=#{PlateId} and Date=#{Date}")
    Integer getRowNumber(Bus bus);
}
