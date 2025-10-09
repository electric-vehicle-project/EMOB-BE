/* EMOB-2025 */
package com.example.emob.constant;

public enum MemberShipLevel {
    BRONZE, SILVER, GOLD, PLATINUM, DELETED, NORMAL;
    public static MemberShipLevel fromPoints(int points) {
        if (points >= 1000) return PLATINUM;
        else if (points >= 500) return GOLD;
        else if (points >= 200) return SILVER;
        else if (points >= 100) return BRONZE;
        else return NORMAL;
    }
}
