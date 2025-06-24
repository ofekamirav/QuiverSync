//
//  ForecastScreen.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import MapKit

public struct ForecastScreen: View {
    
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 34.011_286, longitude: -116.166_868),
        span: MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1)
    )
    
    @State private var searchText = ""
    @State private var searchResults: [MKLocalSearchCompletion] = []
    @State private var selectedCoordinate: CLLocationCoordinate2D? = nil
    @StateObject private var searchDelegateHolder = SearchDelegateHolder()

    
    private var searchCompleter = MKLocalSearchCompleter()
    
    public var body: some View {
        ScrollView {
            VStack(spacing: 10) {
                Text("Forecast")
                    .font(.title3)
                    .fontWeight(.bold)
                    .foregroundColor(AppColors.deepBlue)
                
                // 🧭 Search Input
                TextField("Search for a surf spot", text: $searchText)
                    .padding()
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(style: StrokeStyle(lineWidth: 1)))
                    .onChange(of: searchText) { newValue in
                        print("User typed: \(newValue)")
                        searchCompleter.queryFragment = newValue
                    }
                
                // 🔽 Autocomplete Suggestions
                if !searchResults.isEmpty {
                    List(searchResults, id: \.self) { result in
                        Button {
                            let query = result.title + " " + result.subtitle
                            print("User selected: \(query)")
                            searchForPlace(query)
                        } label: {
                            VStack(alignment: .leading) {
                                Text(result.title).bold()
                                Text(result.subtitle).font(.caption)
                            }
                        }
                        
                    }
                    .frame(height: 150)
                    .listStyle(.plain)
                }
                
                // 🌍 MAP
                Map(coordinateRegion: $region, annotationItems: selectedCoordinate.map { [AnnotatedPlace(coordinate: $0)] } ?? []) { place in
                    MapMarker(coordinate: place.coordinate, tint: .blue)
                }
                .frame(height: 300)
                .cornerRadius(12)
                
                Spacer()
            }
            .padding()
        }
        .onAppear {
            print("ForecastScreen appeared")
            searchCompleter.delegate = searchDelegateHolder
            searchDelegateHolder.onResultsUpdate = { results in
                print("Received \(results.count) autocomplete results")
                self.searchResults = results
            }
        }
        .background(AppColors.background)

    }
    
    // 🔍 Search logic
    private func searchForPlace(_ query: String) {
        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = query
        MKLocalSearch(request: request).start { response, error in
            if let coordinate = response?.mapItems.first?.placemark.coordinate {
                print("Found location: \(coordinate.latitude), \(coordinate.longitude)")
                region.center = coordinate
                selectedCoordinate = coordinate
            } else {
                print("No location found for \(query)")
            }
        }
    }
}

// MARK: - Helper

struct AnnotatedPlace: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}

class SearchDelegateHolder: NSObject, ObservableObject, MKLocalSearchCompleterDelegate {
    var onResultsUpdate: (([MKLocalSearchCompletion]) -> Void)?
    
    func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
        onResultsUpdate?(completer.results)
    }
}



extension UIApplication {
    func endEditing() {
        sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}
