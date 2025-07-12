//
//  LocationSearchService.swift
//  iosApp
//
//  Created by gal levi on 10/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import MapKit
import Combine

class LocationSearchService: NSObject, ObservableObject {
    @Published var query: String = "" {
        didSet {
            handleSearchFragment(query)
        }
    }
    
    @Published var results: [LocationSearchResult] = []
    @Published var status: SearchStatus = .idle
    
    private var completer: MKLocalSearchCompleter

    init(
        filter: MKPointOfInterestFilter = .excludingAll,
        region: MKCoordinateRegion = MKCoordinateRegion(.world),
        types: MKLocalSearchCompleter.ResultType = [.pointOfInterest, .query, .address]
    ) {
        self.completer = MKLocalSearchCompleter()
        super.init()
        completer.delegate = self
        completer.pointOfInterestFilter = filter
        completer.region = region
        completer.resultTypes = types
    }

    private func handleSearchFragment(_ fragment: String) {
        status = .searching
        if !fragment.isEmpty {
            completer.queryFragment = fragment
        } else {
            status = .idle
            results = []
        }
    }
}

// MARK: - MKLocalSearchCompleterDelegate

extension LocationSearchService: MKLocalSearchCompleterDelegate {
    func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
        results = completer.results.map {
            LocationSearchResult(name: $0.title, subtitle: $0.subtitle)
        }
        status = .results
    }

    func completer(_ completer: MKLocalSearchCompleter, didFailWithError error: Error) {
        status = .error(error.localizedDescription)
    }
}

// MARK: - Supporting Models

struct LocationSearchResult: Identifiable, Hashable {
    var id = UUID()
    var name: String
    var subtitle: String
}

enum SearchStatus: Equatable {
    case idle
    case searching
    case results
    case error(String)
}
