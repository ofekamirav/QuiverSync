//
//  AddSpotView.swift
//  iosApp
//
//  Created by gal levi on 18/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//



import SwiftUI
import Shared
import MapKit

struct AddSpotView: View {
    
    let addSpotData: FavoriteSpotForm
    let addSpotViewModel: AddFavSpotViewModel
    @Binding var showAddSpotScreen: Bool
    
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: 34.011_286, longitude: -116.166_868),
        span: MKCoordinateSpan(latitudeDelta: 0.1, longitudeDelta: 0.1)
    )
    
    @State private var searchText = ""
    @State private var searchResults: [MKLocalSearchCompletion] = []
    @State private var selectedCoordinate: CLLocationCoordinate2D? = nil
    @StateObject private var searchDelegateHolder = SearchDelegateHolder()
    @Binding var errorMessage: String

    
//    private var searchCompleter = MKLocalSearchCompleter()
    
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
                        searchDelegateHolder.completer.queryFragment = newValue
                        print("Searching for: \(searchDelegateHolder.completer.queryFragment)")
                    }
                
                // 🔽 Autocomplete Suggestions
                if !searchResults.isEmpty {
                    List(searchResults, id: \.self) { result in
                        Button {
                            let query = result.title + " " + result.subtitle
//                            print("User selected: \(query)")
                            let name = result.title
                            let country = extractCountry(from: result.subtitle)
                            let displayName = "\(name), \(country)"
                            print("Selected place: \(displayName)")
                            searchForPlace(query)
                            addSpotViewModel.onEvent(event: AddFavSpotEventNameChanged(value:displayName))
                            print("Selected coordinates: \(region.center.latitude), \(region.center.longitude)")
                            searchResults = [] // Clear results after selection
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
                
                if selectedCoordinate != nil {
                    Button(action: {
                        print("💾 Save spot button tapped")
                        addSpotViewModel.onEvent(event: AddFavSpotEventSaveClicked())
                    }) {
                        Text("Save this spot!")
                            .foregroundColor(.white)
                            .font(.headline)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(AppColors.deepBlue)
                            .cornerRadius(12)
                            .shadow(radius: 3)
                    }
                    .padding(.top, 16)
                }
            }
            .padding()
        }
        .onAppear {
            print("ForecastScreen appeared")
            searchDelegateHolder.completer.delegate = searchDelegateHolder
            searchDelegateHolder.onResultsUpdate = { results in
                print("Received \(results.count) autocomplete results")
                self.searchResults = results
            }
        }
        .background(AppColors.foamWhite)
        .overlay(
            Group {
                if !errorMessage.isEmpty {
                    VStack {
                        Spacer()
                        Text(errorMessage)
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.red.opacity(0.9))
                            .cornerRadius(12)
                            .padding(.bottom, 30)
                            .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            errorMessage = ""
                        }
                    }
                }
            }
        )


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
                addSpotViewModel.onEvent(event: AddFavSpotEventLocationChanged(longitude: coordinate.longitude, latitude: coordinate.latitude))
            } else {
                print("No location found for \(query)")
            }
        }
    }
    
}

// MARK: - Helper

private func extractCountry(from subtitle: String) -> String {
    let components = subtitle.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }
    return components.last ?? subtitle
}


struct AnnotatedPlace: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}

class SearchDelegateHolder: NSObject, ObservableObject, MKLocalSearchCompleterDelegate {
    let completer = MKLocalSearchCompleter()

    override init() {
        super.init()
        completer.delegate = self
    }

    var onResultsUpdate: (([MKLocalSearchCompletion]) -> Void)?

    func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
        onResultsUpdate?(completer.results)
    }

    private func completer(_ completer: MKLocalSearchCompleter, didFailWithError error: Error) {
        print("❌ Autocomplete error: \(error.message)")
    }
    
}




extension UIApplication {
    func endEditing() {
        sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}
