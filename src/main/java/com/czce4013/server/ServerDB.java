package com.czce4013.server;

import com.czce4013.entity.ClientInfo;
import com.czce4013.entity.ClientQuery;
import com.czce4013.entity.DateTime;
import com.czce4013.entity.FlightInfo;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDB {
    private static ArrayList<FlightInfo> flightData= new ArrayList<FlightInfo>();;
    private static HashMap<Integer, List<ClientInfo>> callbackList = new HashMap<>();

    public ServerDB(){
        seed();
    }

    public static void seed(){
        DateTime dateTime = new DateTime(2019,3,28,23,5);
        flightData.add(new FlightInfo((short) 1001,"SINGAPORE","BANGKOK",dateTime,1.23F,(short) 5));
        dateTime = new DateTime(2019,3,28,23,10);
        flightData.add(new FlightInfo((short) 2012,"KL","BANGKOK",dateTime,2.23F,(short) 15));
        dateTime = new DateTime(2019,3,28,23,15);
        flightData.add(new FlightInfo((short) 3197,"SINGAPORE","KL",dateTime,3.23F,(short) 2));
        dateTime = new DateTime(2019,3,28,23,20);
        flightData.add(new FlightInfo((short) 4456,"BANGKOK","SINGAPORE",dateTime,4.23F,(short) 9));
        dateTime = new DateTime(2019,3,28,23,25);
        flightData.add(new FlightInfo((short) 5232,"SINGAPORE","BANGKOK",dateTime,5.23F,(short) 7));
    }

    public List findFlightID(String source, String dest){
        ArrayList<FlightInfo> returnArray = new ArrayList<FlightInfo>();
        for (FlightInfo flight:flightData){
            if (flight.getSource().equals(source) && flight.getDest().equals(dest)){
                returnArray.add(flight);
            }
        }
        return returnArray;
    }

    public List getFlightDetails(int id){
        ArrayList<FlightInfo> returnArray = new ArrayList<FlightInfo>();
        for (FlightInfo flight:flightData){
            if (flight.getId() == id){
                returnArray.add(flight);
            }
        }
        return returnArray;
    }

    public List makeReservation(int id, int seatsReserve) {
        ArrayList<FlightInfo> returnArray = new ArrayList<FlightInfo>();
        for (FlightInfo flight:flightData){
            if (flight.getId() == id){
                flight.setSeatsAvailable((short) (flight.getSeatsAvailable()-seatsReserve));
                returnArray.add(flight);
            }
        }
        return returnArray;
    }

    public void observeFlight(int flightId,SocketAddress socketAddress, int timeout) {
        if (!callbackList.containsKey(flightId)){
            callbackList.put(flightId,new ArrayList<>());
        }
        List<ClientInfo> addresses = callbackList.get(flightId);
        addresses.add(new ClientInfo(socketAddress, timeout));
        callbackList.put(flightId,addresses);
    }

    public static List<SocketAddress> getCallBackAddresses(int flightID){
        return callbackList.get(flightID).stream().map(ClientInfo::getSocket).collect(Collectors.toList());
    }

    public static void filterCallBackAddresses() {
        callbackList.forEach((flight, clients) -> {
            callbackList.put(flight, clients.stream().filter((c) -> c.getExpire() > System.currentTimeMillis()).collect(Collectors.toList()));
        });
    }
}