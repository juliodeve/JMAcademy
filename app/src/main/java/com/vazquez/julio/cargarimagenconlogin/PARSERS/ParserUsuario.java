package com.vazquez.julio.cargarimagenconlogin.PARSERS;

import com.vazquez.julio.cargarimagenconlogin.POJOS.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParserUsuario {
    public static List<Usuario> parser (String content){

        try {
            JSONArray jsonArray = new JSONArray(content);
            List<Usuario> usuarioList = new ArrayList<>();

            for (int i =0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Usuario usuario = new Usuario();

                usuario.setNombre(jsonObject.getString("nombre"));
                usuario.setEdad(jsonObject.getInt("edad"));
                usuario.setSexo(jsonObject.getString("sexo"));
                usuario.setTelefono(jsonObject.getInt("tel"));
                usuario.setFecha_nacimiento(jsonObject.getString("fechaN"));
                usuario.setDireccion(jsonObject.getString("direc"));
                usuarioList.add(usuario);
            }
            return usuarioList;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }
}
