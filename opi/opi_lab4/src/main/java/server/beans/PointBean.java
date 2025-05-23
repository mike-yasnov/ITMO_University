package server.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import server.data.DataBase;
import server.models.Point;
import server.utils.Area;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("pointBean")
@ApplicationScoped
public class PointBean {

    private Double x;
    private Double y;
    private Double r;

    // Инициализация MBean-компонентов при загрузке класса
    static {
        MBeanRegistrator.registerMBeans();
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }


    public void addPoint() {
        try(var connection = DataBase.connect()){
            if (!Area.validation(x,y,r)) {
                return;
            }

            long started = System.nanoTime();
            boolean hit = Area.calculate(x, y, r);
            long ended = System.nanoTime();
            DataBase.insertPoint(connection,hit,x,y,r,LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")),(ended - started) / 1000);
            
            // Обновляем статистику в MBean
            MBeanRegistrator.getPointStatisticsMBean().addPoint(hit);
        }catch(SQLException e ){
            e.printStackTrace();
        }
    }

}
