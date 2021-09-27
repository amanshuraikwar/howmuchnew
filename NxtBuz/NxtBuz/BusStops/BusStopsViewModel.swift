//
//  BusStopsViewModel.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import Foundation
import iosUmbrella
import CoreLocation

class BusStopsViewModel : NSObject, ObservableObject, CLLocationManagerDelegate {
    @Published var busStopsScreenState: BusStopsScreenState = .Fetching(message: "Fetching bus stops...")
    private let getBusStopsUseCase = Di.get().getBusStopsUseCase()
    
    private let locationManager: CLLocationManager

    override init() {
        locationManager = CLLocationManager()
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }
    
    func fetchBusStops() {
        var lat = -1.0
        var lng = -1.0
        
        switch busStopsScreenState {
        case .Fetching, .AskLocationPermission, .Error, .GoToSettingsLocationPermission:
            switch locationManager.authorizationStatus {
            case .notDetermined:
                busStopsScreenState = .AskLocationPermission
            case .restricted:
                busStopsScreenState = .Error(
                    errorMessage: "Location use is restricted, please try again."
                )
            case .denied:
                busStopsScreenState = .GoToSettingsLocationPermission
            case .authorizedAlways, .authorizedWhenInUse:
                lat = locationManager.location?.coordinate.latitude ?? -1.0
                lng = locationManager.location?.coordinate.longitude ?? -1.0
            default:
                busStopsScreenState = .Error(
                    errorMessage: "Something went wrong, please try again."
                )
            }
        
            if lat == -1.0 || lng == -1.0 {
                return
            }
            
            getBusStopsUseCase.invoke(
                lat: lat,
                lon: lng,
                limit: 50
            ) { busStopList in
                DispatchQueue.main.sync {
                    self.busStopsScreenState =
                        .Success(
                            header: "Nearby Bus Stops",
                            busStopList: busStopList
                        )
                }
            }
        default:
            break
        }
    }
    
    func requestPermission() {
        locationManager.requestWhenInUseAuthorization()
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        self.busStopsScreenState = .Fetching(message: "Fetching nearby bus stops...")
        self.fetchBusStops()
    }
    
    func searchBusStops(_ searchString: String) {
        Di.get()
            .getSearchUseCase()
            .invoke(
                query: searchString,
                limit: 50
            ) { searchResult in
                DispatchQueue.main.sync {
                    self.busStopsScreenState =
                        .Success(
                            header: "Matching Bus Stops",
                            busStopList: searchResult.busStopList
                        )
                }
            }
    }
    
    func onSearch(searchString: String) {
        if searchString == "" {
            self.busStopsScreenState = .Fetching(message: "Fetching nearby bus stops...")
            fetchBusStops()
        } else {
            self.busStopsScreenState = .Fetching(message: "Searching bus stops...")
            searchBusStops(searchString)
        }
    }
}

enum BusStopsScreenState {
    case Fetching(message: String)
    case AskLocationPermission
    case Error(errorMessage: String)
    case GoToSettingsLocationPermission
    case Success(header: String, busStopList: [BusStop])
}
