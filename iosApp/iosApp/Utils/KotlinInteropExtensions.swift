//
//  KotlinInteropExtensions.swift
//  iosApp
//
//  Created by gal levi on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Shared

extension Data {
    func toKotlinByteArray() -> KotlinByteArray {
        let kotlinByteArray = KotlinByteArray(size: Int32(self.count))
        for (index, byte) in self.enumerated() {
            let int8Value = Int8(bitPattern: byte) // Converts UInt8 to Int8 safely
            kotlinByteArray.set(index: Int32(index), value: int8Value)
        }
        return kotlinByteArray
    }
}
