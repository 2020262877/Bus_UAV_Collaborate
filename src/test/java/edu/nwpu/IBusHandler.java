package edu.nwpu;

import edu.nwpu.bean.Path;
import edu.nwpu.domain.Bus;

import java.util.List;

public interface IBusHandler {


    /**
     * 分割出每条公交车的路径，将结果保存到列表 paths中
     * path{起始点，终止点，距离用户最近的点，距离餐厅最近的点，距离无人机仓库最近的点}
     *
     * @param buses 该Bus所有数据点
     * @throws Exception phaseException
     */
    void findBreakPoint(List<Bus> buses) throws Exception;

    /**
     * 计算一条路径 {起始点，终止点，距离用户最近的点，距离餐厅最近的点，距离无人机仓库最近的点}
     *
     * @param buses      该Bus所有数据点
     * @param startPoint 该路径的起始点
     * @param endPoint   该路径的起始点
     * @return Path
     * @throws Exception phaseException
     */
    Path calculatePath(List<Bus> buses, Bus startPoint, Bus endPoint) throws Exception;

    /**
     * 计算某路径上距离某个坐标点最近的一个点
     *
     * @param buses      该Bus所有数据点
     * @param startIndex 该路径的起始下标
     * @param endIndex   该路径的终止下标
     * @param latitude   无人机仓库/用户/餐厅的纬度
     * @param longitude  无人机仓库/用户/餐厅的经度
     * @return 最近的数据点
     */
    Bus calculateMinDistance(List<Bus> buses, int startIndex, int endIndex, double latitude, double longitude);

    /**
     * 计算某路径上距离某个坐标点距离的升序排列
     *
     * @param buses      该Bus所有数据点
     * @param startIndex 该路径的起始下标
     * @param endIndex   该路径的终止下标
     * @param latitude   无人机仓库/用户/餐厅的纬度
     * @param longitude  无人机仓库/用户/餐厅的经度
     * @return 距离的升序排列
     */
    List<Bus> calculateDistanceList(List<Bus> buses, int startIndex, int endIndex, double latitude, double longitude);

    /**
     * 读取Mybatis xml配置文件，调用findBreakPoint()
     * @throws Exception
     */
    void main() throws Exception;
}
