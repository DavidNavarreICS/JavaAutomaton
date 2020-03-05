/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.jautomaton;

/**
 *
 * @author David
 */
public interface Precondition {

    /**
     * Verify the precondition.
     *
     * @param parameters the parameters
     * @return <b>true</b> if the precondition is verified according to the
     * given parameters.
     */
    boolean isVerified(Object... parameters);
}
