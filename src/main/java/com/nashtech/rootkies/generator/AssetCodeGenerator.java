package com.nashtech.rootkies.generator;

import com.nashtech.rootkies.model.Asset;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
@Setter
public class AssetCodeGenerator implements IdentifierGenerator {
    private Short maxID = 0;


    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Connection connection = session.connection();
        Asset asset = (Asset) object;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from assets a where a.assetcode like '"
                    + asset.getCategory().getCategoryCode() + "%' order by a.assetcode desc");
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean flag = true;

            while (resultSet.next()){
                String suffix = resultSet.getString("assetcode").substring(asset.getCategory().getCategoryCode().length());
                try {
                    Short numberPart = Short.parseShort(suffix);
                    setMaxID(numberPart);
                    flag = false;
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (flag) {
                setMaxID((short) 1);
            } else {
                setMaxID((short) (getMaxID() + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asset.getCategory().getCategoryCode() + String.format("%06d", getMaxID());
    }
}
